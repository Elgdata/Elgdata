package models.statistics;

/**
 * 
 * @author Haakon Objects used for generating the StackColumnGraph
 */
public class StackColumnElk {
	public String	year;
	public int		adultMale;
	public int		adultFemale;
	public int		youngMale;
	public int		youngFemale;
	public int		calfMale;
	public int		calfFemale;
	public int		unknownSex;
	public int		unknownFemale;
	public int		unknownMale;
	public int		calfCount;
	public int		youngCount;
	public int		adultCount;

	public StackColumnElk() {
		year = "";
		adultMale = 0;
		adultFemale = 0;
		youngMale = 0;
		youngFemale = 0;
		calfMale = 0;
		calfFemale = 0;
		unknownSex = 0;
		unknownFemale = 0;
		unknownMale = 0;
		calfCount = 0;
		youngCount = 0;
		adultCount = 0;
	}
}