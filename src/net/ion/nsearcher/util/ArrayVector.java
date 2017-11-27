package net.ion.nsearcher.util;

public class ArrayVector {

	private double[] data;

	public ArrayVector(int size) {
		data = new double[size];
	}

    public ArrayVector(ArrayVector v, boolean deep) {
        data = deep ? v.data.clone() : v.data;
    }

	public void setEntry(int index, double value) {
		data[index] = value;
	}

	public int getDimension() {
		return data.length;
	}

	public double getNorm() {
		double sum = 0;
		for (double a : data) {
			sum += a * a;
		}
		return Math.sqrt(sum);
	}

	public double getL1Norm() {
		double sum = 0;
		for (double a : data) {
			sum += Math.abs(a);
		}
		return sum;
	}

	public double getLInfNorm() {
		double max = 0;
		for (double a : data) {
			max = Math.max(max, Math.abs(a));
		}
		return max;
	}
	
    public ArrayVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }
    
    public ArrayVector copy() {
        return new ArrayVector(this, true);
    }

    public ArrayVector mapDivideToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] /= d;
        }
        return this;
    }
    
    public double dotProduct(ArrayVector v) {
        final double[] vData = v.data;
        double dot = 0;
        for (int i = 0; i < data.length; i++) {
            dot += data[i] * vData[i];
        }
        return dot;
    }
}
