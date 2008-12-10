/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.util;

/**
 * Strategy to implement filtering on a per instance level of granularity.
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public interface Filter<P> {

	/**
	 * @param param the object to check
	 * @return <code>true</code> if the object fulfils the filter criteria
	 */
	boolean matches(P param);
	
	/**
	 * Static utilities for filters.
	 * @author Sebastian Zarnekow - Initial contribution and API
	 */
	public static class Util {
		
		public static <T> Filter<T> notNull() {
			return new Filter<T>() {
				public boolean matches(T param) {
					return param != null;
				}
			};
		}
		
		public static <T> Filter<T> instanceOf(final Class<? extends T> clazz) {
			return new Filter<T>() {
				public boolean matches(T param) {
					return clazz.isInstance(param);
				}
			};
		}
		
		public static <T> Filter<T> not(final Filter<T> input) {
			return new Filter<T>() {
				public boolean matches(T param) {
					return !input.matches(param);
				}
			};
		}
		
		public static <T> Filter<T> and(final Filter<T> ... input) {
			return new Filter<T>() {
				public boolean matches(T param) {
					for(Filter<T> in: input) {
						if (!in.matches(param))
							return false;
					}
					return input.length > 0;
				}
			};
		}
		
		public static <T> Filter<T> or(final Filter<T> ... input) {
			return new Filter<T>() {
				public boolean matches(T param) {
					for(Filter<T> in: input) {
						if (in.matches(param))
							return true;
					}
					return false;
				}
			};
		}
	}
}
