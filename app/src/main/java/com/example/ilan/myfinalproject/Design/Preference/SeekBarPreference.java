package com.example.ilan.myfinalproject.Design.Preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ilan.myfinalproject.R;

import java.util.Locale;


//SeekBar Preference ditails with working process
public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener
{
private static final int MIN_STEP_SIZE=1;
private static final int DEFAULT_MAX_PROGRESS=100;

private int progress,minProgress,maxProgress,stepSize;
private OnSummaryChangeListener summaryChangeListener;
private boolean isSummaryFormat;
private TextView summary;
private SeekBar seekBar;


    public SeekBarPreference(Context context)
    {
    super(context);

    init();
    }


    public SeekBarPreference(Context context, AttributeSet attrs)
    {
    super(context, attrs);

    init();
    }


	private void init()
	{
	setLayoutResource(R.layout.seekbar);

	progress=0;
	minProgress=0;
	maxProgress=DEFAULT_MAX_PROGRESS;
	stepSize=MIN_STEP_SIZE;

	isSummaryFormat=false;

	summaryChangeListener=null;
	}


	@Override
    protected void onBindView(View view)
    {
    super.onBindView(view);

    summary=(TextView) view.findViewById(android.R.id.summary);
	summary.setVisibility(View.VISIBLE);

	setSummaryText(progress);

    seekBar=(SeekBar) view.findViewById(R.id.seekbar);
    seekBar.setMax(maxProgress);
    seekBar.setProgress(progress-minProgress);
    seekBar.setOnSeekBarChangeListener(this);
    }


	private void setSummaryText(int progress)
	{
	CharSequence baseSummary;
	String summaryText;

	if(summary == null)
		{
		return;
		}

	if(summaryChangeListener == null)
		{
		baseSummary=getSummary();

		if(baseSummary != null)
			{

			if(isSummaryFormat)
				{
				summaryText=String.format(Locale.getDefault(), baseSummary.toString(), progress);
				}else
					{
					summaryText=baseSummary.toString()+ ": " + progress;
					}
			}else
				{
				summaryText=String.valueOf(progress);
				}
		}else
			{
			summaryText=summaryChangeListener.onUpdateSummary(progress);
			}

	summary.setText(summaryText);
	}


	public void setOnSummaryChangeListener(OnSummaryChangeListener summaryChangeListener)
	{
	this.summaryChangeListener=summaryChangeListener;
	}


	public void setSummaryFormatEnabled(boolean enabled)
	{
	isSummaryFormat=enabled;
	}


	public void setStepSize(int stepSize)
	{
	this.stepSize= (stepSize > 0)? stepSize : MIN_STEP_SIZE;
	}


	public void setLimitValue(int minValue, int maxValue)
	{
	minProgress=minValue;

	setMaxValue(maxValue-minValue);
	}


	public void setMaxValue(int maxValue)
	{
	maxProgress=maxValue;

	if(seekBar!=null)
		{
		seekBar.setMax(maxProgress);
		}
	}


	public int getRealProgress(int progress)
	{
	progress+=minProgress;

	progress=(progress/stepSize) * stepSize;

	return progress;
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
	progress=getRealProgress(progress);

	setSummaryText(progress);
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	int progress=getRealProgress(seekBar.getProgress());

	seekBar.setProgress(progress-minProgress);

	setValue(progress);
	}


	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
    setValue(restoreValue ? getPersistedInt(progress) : (Integer) defaultValue);
	}


	public void setValue(int value)
	{
	if (shouldPersist())
		{
		persistInt(value);
		}

	if (value !=progress)
		{
		progress= value;
		}
	}


	public interface OnSummaryChangeListener
	{

	String onUpdateSummary(int value);

	}

}
