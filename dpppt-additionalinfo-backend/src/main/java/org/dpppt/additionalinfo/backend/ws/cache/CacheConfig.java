package org.dpppt.additionalinfo.backend.ws.cache;

/** Holds the request header cache constants */
public class CacheConfig {

	private static final int SECOND = 1;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	private static final int YEAR = 365 * DAY;

	public static final int NEXT_REFRESH_STATISTICS = 1 * DAY;
	public static final int MAX_AGE_STATISTICS = 5 * MINUTE;
}
