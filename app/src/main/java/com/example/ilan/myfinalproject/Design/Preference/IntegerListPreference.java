package com.example.ilan.myfinalproject.Design.Preference;

import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


public class IntegerListPreference extends ListPreference
{
private int defaultValue;


    public IntegerListPreference(Context context)
    {
    super(context);
    }


    public IntegerListPreference(Context context, AttributeSet attrs)
    {
    super(context, attrs);
    }


    @Override
    protected void onBindView(View view)
    {
    super.onBindView(view);

    TextView title=(TextView)view.findViewById(android.R.id.title);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
        title.setTextAppearance(view.getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        }else
            {
            title.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
            }
    }


    @Override
    public void setDefaultValue(Object defaultValue)
    {

    if(defaultValue instanceof Integer)
        {
        this.defaultValue=(int) defaultValue;
        }
    }


    @Override
    protected boolean persistString(String value)
    {
    boolean isPersistent=false;

    if(value != null)
        {
        isPersistent=persistInt(Integer.valueOf(value));
        }

    return isPersistent;
    }


    @Override
    protected String getPersistedString(String defaultReturnValue)
    {
    int intValue;
    String value;

    if(getSharedPreferences().contains(getKey()))
        {
        intValue = getPersistedInt(defaultValue);
        value=String.valueOf(intValue);
        }else
            {
            value=defaultReturnValue;
            }

    return value;
    }


}
