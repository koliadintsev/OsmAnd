package com.operasoft.snowboard.services;

import android.os.IBinder;

public class ServiceHook {
	
	public IBinder ServiceBinding;
	public String ClassName;
	boolean Bound = false;
	
	public ServiceHook(String strClass, IBinder srvBinding)
	{
		ServiceBinding=srvBinding;
		ClassName=strClass;
		Bound=true;
	}
	
	public boolean IsAlive()
	{
		try{
			if(ServiceBinding.isBinderAlive())
			{
				return ServiceBinding.pingBinder();
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	


}
