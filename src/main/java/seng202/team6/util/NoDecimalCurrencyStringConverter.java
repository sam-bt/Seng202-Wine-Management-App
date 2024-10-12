package seng202.team6.util;

import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.converter.CurrencyStringConverter;

/**
 * CurrencyStringConverter without trailing zeros.
 * <p>
 * Makes displaying price on sliders look nice
 * </p>
 */
public class NoDecimalCurrencyStringConverter extends CurrencyStringConverter {

  private final NumberFormat numberFormat;

  /**
   * Constructor.
   */
  public NoDecimalCurrencyStringConverter() {
    numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    numberFormat.setMaximumFractionDigits(0);
  }

  @Override
  public String toString(Number value) {
    return numberFormat.format(value);
  }
}
