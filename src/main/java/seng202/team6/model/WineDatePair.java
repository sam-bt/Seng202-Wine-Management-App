package seng202.team6.model;

import java.sql.Date;

/**
 * Pair of a wine and date
 * <p>
 * Used in wine lists to associate with date added
 * </p>
 *
 * @param wine wine
 * @param date date
 */
public record WineDatePair(Wine wine, Date date) {

}
