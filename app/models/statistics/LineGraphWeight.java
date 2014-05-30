package models.statistics;

/**
 * 
 * @author Haakon Objects used for generating the StackColumnGraph
 */
public class LineGraphWeight {
	public String	year;
	public double	adultMaleWeight;
	public double	adultFemaleWeight;
	public double	youngMaleWeight;
	public double	youngFemaleWeight;
	public double	calfMaleWeight;
	public double	calfFemaleWeight;
	public double	unknownMaleWeight;
	public double	unknownFemaleWeight;
	public double	averageWeightCalf;
	public double	averageWeightYoung;
	public double	averageWeightAdult;
	public double   sumWeight;

	public LineGraphWeight() {
		year = "";
		adultMaleWeight = 0;
		adultFemaleWeight = 0;
		youngMaleWeight = 0;
		youngFemaleWeight = 0;
		calfMaleWeight = 0;
		calfFemaleWeight = 0;
		unknownMaleWeight = 0;
		unknownFemaleWeight = 0;
		averageWeightCalf = 0;
		averageWeightYoung = 0;
		averageWeightAdult = 0;
		sumWeight = 0;
	}
}