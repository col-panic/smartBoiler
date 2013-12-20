package at.fhv.smartdevices.commons;

public interface INumericMetric {
	<T extends Number> float calculateDistance(T sample1, T sample2);
}
