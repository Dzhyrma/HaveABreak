package animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;

public class ExpandAnimation extends Animation implements AnimationListener {
	private int _targetHeight;
	private View _view;
	private boolean _down;

	public ExpandAnimation(View view, int targetHeight) {
		setAnimationListener(this);
		_view = view;
		_targetHeight = targetHeight;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int newHeight;
		if (_down) {
			newHeight = (int) (_targetHeight * interpolatedTime);
		} else {
			newHeight = (int) (_targetHeight * (1 - interpolatedTime));
		}

		_view.getLayoutParams().height = newHeight;
		if (newHeight >= _targetHeight)
			_view.getLayoutParams().height = -1;
		_view.requestLayout();
	}

	public void setTargetHeight(int h) {
		_targetHeight = h;
	}

	public ExpandAnimation expand() {
		_down = true;
		return this;
	}

	public ExpandAnimation collapse() {
		_down = false;
		return this;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}

	public void onAnimationEnd(Animation animation) {

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}
}