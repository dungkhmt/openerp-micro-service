package localsearch.functions.multiknapsack;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MultiKnapsackStatistic extends AbstractInvariant implements IFunction {

	private int _value;
	private VarIntLS[] _x;
	private int[] _w;
	private int[] _tw;// _tw[i] is the sum of weights of items of the bin i
	private int _n;// number of items
	private int _m;// number of bins
	private HashMap<VarIntLS, Integer> _map;
	private LocalSearchManager _lsm;
	private int _statisticNumber;

	public MultiKnapsackStatistic(VarIntLS[] x, int[] w, int knapsackNumber,  int statisticNumber) {
		_x = x;
		_w = w;
		_n = x.length;
		_m = knapsackNumber;
		_lsm = _x[0].getLocalSearchManager();
		_statisticNumber = statisticNumber;

		post();
		_lsm.post(this);
		
		this.initPropagate();
	}

	private void post() {
		_map = new HashMap<VarIntLS, Integer>();
		for (int j = 0; j < _n; j++) {
			_map.put(_x[j], j);
		}
		_tw = new int[_m];
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		System.err.println("MultiKnapsackStatistic::Warning: getMinValue return default value");
		return 0;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		System.err.println("MultiKnapsackStatistic::Warning: getMaxValue return default value");
		return _m;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (!_map.containsKey(x) || x.getValue() == val)
			return 0;

		int tw1 = _tw[x.getValue()];
		int tw2 = _tw[val];

		int w = _w[_map.get(x)];

		int delta = 0;
		if (tw1 == this._statisticNumber) {
			if (w != 0) {
				delta++;
			}
		} else if (tw1 - w == this._statisticNumber) {
			delta--;
		}

		if (tw2 == this._statisticNumber) {
			if (w != 0) {
				delta++;
			}
		} else if (tw2 + w == this._statisticNumber) {
			delta--;
		}

		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if ((!_map.containsKey(x) && !_map.containsKey(y)) || x.getValue() == y.getValue())
			return 0;
		if (!_map.containsKey(x) && _map.containsKey(y))
			return this.getAssignDelta(y, x.getValue());
		if (_map.containsKey(x) && !_map.containsKey(y))
			return this.getAssignDelta(x, y.getValue());

		int twx = _tw[x.getValue()];
		int twy = _tw[y.getValue()];

		int wx = _w[_map.get(x)];
		int wy = _w[_map.get(x)];

		int delta = 0;
		if (twx == this._statisticNumber) {
			if (wy - wx != 0) {
				delta++;
			}
		} else if (twx - wx + wy == this._statisticNumber) {
			delta--;
		}

		if (twy == this._statisticNumber) {
			if (wx - wy != 0) {
				delta++;
			}
		} else if (twy + wx - wy == this._statisticNumber) {
			delta--;
		}

		return delta;
	}

	public String name() {
		return "MultiKnapsackStatistic";
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (!_map.containsKey(x) || x.getValue() == val) {
			return;
		}

		_value = _value + this.getAssignDelta(x, val);
		
		// update tw
	}

	@Override
	public void initPropagate() {
		_value = 0;
		for (int j = 0; j < _n; j++) {
			int bx = _x[j].getValue();
			_tw[bx] += _w[j];
		}

		for (int i = 0; i < _m; i++) {
			if (_tw[i] == this._statisticNumber) {
				_value++;
			}
		}
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		return _lsm;
	}

	@Override
	public boolean verify() {
		return true;
	}

}
