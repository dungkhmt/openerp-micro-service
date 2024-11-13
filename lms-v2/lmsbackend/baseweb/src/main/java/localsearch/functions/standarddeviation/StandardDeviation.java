package localsearch.functions.standarddeviation;

import localsearch.functions.basic.FuncVarConst;
import localsearch.model.*;

import java.util.HashSet;

public class StandardDeviation extends AbstractInvariant implements IFloatFunction {

	private double _value;
	private LocalSearchManager _ls;
	private IFunction[] _f;
	private HashSet<VarIntLS> _x;

	public StandardDeviation(IFunction[] f) {
		_f = f;
		_ls = f[0].getLocalSearchManager();
		post();
	}

	public StandardDeviation(VarIntLS[] x) {
		int n = x.length;
		_f = new IFunction[n];
		for (int i = 0; i < n; i++) {
			_f[i] = new FuncVarConst(x[i]);
		}
		_ls = x[0].getLocalSearchManager();
		post();
	}

	private void post() {
		_x = new HashSet<VarIntLS>();
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] vars = _f[i].getVariables();
			if (vars != null) {
				for (VarIntLS v : vars) {
					_x.add(v);
				}
			}
		}
		_ls.post(this);
	}

	@Override
	public double getMinValue() {
		// TODO Auto-generated method stub
		System.err.println("STD::getMinValue: return default value.");
		return 0;
	}

	@Override
	public double getMaxValue() {
		// TODO Auto-generated method stub
		System.err.println("STD::getMaxValue: return default value.");
		return Double.MAX_VALUE;
	}

	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return this._value;
	}

	@Override
	public double getAssignDelta(VarIntLS x, int val) {
		if (!_x.contains(x))
			return 0;

		double mean = 0;
		double tmp[] = new double[_f.length];
		for (int i = 0; i < _f.length; i++) {
			tmp[i] = _f[i].getAssignDelta(x, val) + _f[i].getValue();
			mean += tmp[i];
		}
		mean /= _f.length;

		double std = 0;
		for (int i = 0; i < _f.length; i++) {
			std += Math.pow(tmp[i] - mean, 2);
		}
		std = Math.sqrt(std / _f.length);

		return std - _value;
	}

	@Override
	public double getSwapDelta(VarIntLS x, VarIntLS y) {
		if (!_x.contains(x) && !_x.contains(y))
			return 0;
		if (!_x.contains(x) && _x.contains(y))
			return this.getAssignDelta(y, x.getValue());
		if (_x.contains(x) && !_x.contains(y))
			return this.getAssignDelta(x, y.getValue());

		double mean = 0;
		double tmp[] = new double[_f.length];
		for (int i = 0; i < _f.length; i++) {
			tmp[i] = _f[i].getSwapDelta(x, y) + _f[i].getValue();
			mean += tmp[i];
		}
		mean /= _f.length;

		double std = 0;
		for (int i = 0; i < _f.length; i++) {
			std += Math.pow(tmp[i] - mean, 2);
		}

		std = Math.sqrt(std / _f.length);

		return std - _value;
	}

	@Override
	public double getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (!_x.contains(x))
			return;
		_value = _value + this.getAssignDelta(x, val);
	}

	public String name() {
		return "StandardDeviation";
	}

	@Override
	public void initPropagate() {
		double mean = 0;
		for (int i = 0; i < _f.length; i++) {
			mean += _f[i].getValue();
		}
		mean /= _f.length;

		double std = 0;
		for (int i = 0; i < _f.length; i++) {
			std += Math.pow(_f[i].getValue() - mean, 2);
		}

		std = Math.sqrt(std / _f.length);

		_value = std;
	}

	@Override
	public VarIntLS[] getVariables() {
		VarIntLS[] x = new VarIntLS[_x.size()];
		int idx = 0;
		for (VarIntLS v : _x) {
			x[idx++] = v;
		}
		return x;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		return _ls;
	}

	@Override
	public boolean verify() {
		return true;
	}

}
