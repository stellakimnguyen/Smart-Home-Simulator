package models;

/**
 * Store a temperature value.
 * ===Attributes===
 * `temperature (private int):` The value of the temperature * 100. Represents a two digit precision decimal.
 *
 * @version 3
 * @author Rodrigo M. Zanini (40077727)
 * @author Pierre-Alexis Barras (40022016)
 * @author Mohamed Amine Kihal (40046046)
 * @author Stella Nguyen (40065803)
 */
public class Temperature implements Comparable<Temperature>{
  private int temperature;

  public Temperature() {
    this.temperature = 2000;
  }

  /**
   * Get the temperature, in centigrades.
   */
  public int getTemperature() {
    return temperature;
  }

  /**
   * Set the temperature, in centigrades.
   */
  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  /**
   * Change the temperature by an offset, in centigrades.
   */
  public void offsetTemperature(int offset) {
    temperature += offset;
  }

  /**
   * Get the [[java.lang.String String]] representation of the temperature.
   */
  public String getTemperatureString() {
    int base = temperature/100;
    int decimal = temperature%100;
    return base + "." + (decimal<10?"0"+decimal:decimal);
  }

  @Override
  public int compareTo(Temperature o) {
    return (this.temperature - o.temperature);
  }
}
