// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package es.efor.plandifor.demo;


public class DemoDescription
{

	private String className;
	private String description;

	public DemoDescription(String s, String s1)
	{
		className = s;
		description = s1;
	}

	public String getClassName()
	{
		return className;
	}

	public String getDescription()
	{
		return description;
	}

	public String toString()
	{
		return description;
	}
}
