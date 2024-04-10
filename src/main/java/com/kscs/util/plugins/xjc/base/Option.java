/*
 * MIT License
 *
 * Copyright (c) 2014 Klemm Software Consulting, Mirko Klemm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kscs.util.plugins.xjc.base;

import java.lang.reflect.Field;

import org.glassfish.jaxb.core.api.impl.NameConverter;

/**
 * @author Mirko Klemm 2015-02-13
 */
public abstract class Option<T> {
	protected final AbstractPlugin plugin;
	protected final Field field;
	protected final String name;
	protected final String choice;
	protected static final NameConverter NAME_CONVERTER = new NameConverter.Standard();

	protected Option(final String name, final AbstractPlugin plugin, final Field field, final String choice) {
		this.name = name;
		this.plugin = plugin;
		this.field = field;
		this.choice = choice;
	}

	public String getName() {
		return this.name;
	}

	public abstract void setStringValue(final String s);
	public abstract String getStringValue();

	public void set(final T v) {
		this.field.setAccessible(true);
		try {
			this.field.set(this.plugin, v);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public String getChoice() {
		return this.choice;
	}

	public T get() {
		try {
			this.field.setAccessible(true);
			final T val =  (T) this.field.get(this.plugin);
			return val;
		} catch(final IllegalAccessException e) {
			return null;
		}
	}

	public boolean tryParse(final String arg) {
		final String pluginName = this.plugin.getOptionName().substring(1).toLowerCase();
		final int equalsIndex = arg.indexOf('=');
		final String optionName = arg.substring(1 + pluginName.length(), equalsIndex > 0 ? equalsIndex : arg.length());
		if(arg.toLowerCase().startsWith("-" + pluginName) && (this.name.equalsIgnoreCase(optionName) || this.name.equals(Option.NAME_CONVERTER.toVariableName(optionName)) )) {
			if(equalsIndex > 0) {
				setStringValue(arg.substring(equalsIndex+1));
			} else {
				setStringValue("y");
			}
			return true;
		} else {
			return false;
		}
	}

	private String getOptionName(final String pluginName, final String arg) {
		return arg.substring(pluginName.length());
	}
}

