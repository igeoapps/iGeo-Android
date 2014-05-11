package com.bitcliq.igeo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Esta classe extende a HorizontalScrollView e tem como vantagem ter implementado um listener que permite
 * saber quando o scroll parou. Poderá ser usada para implementação de swipe
 * @author Bitcliq, Lda.
 *
 */
public class IGEOHorizontalScrollView extends HorizontalScrollView {

	/**
	 * Task que verifica se o scroll já parou.
	 */
	private static Runnable scrollerTask;
	
	/**
	 * Posição inicial antes de iniciar o scroll.
	 */
	private int initialPosition;
	
	/**
	 * Intervalo de verificação.
	 */
	private static int newChek = 50;
	
	/**
	 * Indica se o scroll já parou ou não.
	 */
	private static boolean stopTask = false;


	public interface onScrollStopedListner{
		void onScrollStoped();
	}

	private onScrollStopedListner onScrollStoped;

	public IGEOHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public IGEOHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		scrollerTask = new Runnable() {
			public void run() {
				
				if (stopTask)
					return;
				
				// TODO Auto-generated method stub
				int newPosition = getScrollX();

				if ( initialPosition - newPosition == 0 ) {
					if ( onScrollStoped != null ) {
						onScrollStoped.onScrollStoped();
					}
				} else{
					initialPosition = getScrollX();
					postDelayed(scrollerTask, newChek);

				}

			}
		};
	}
	public IGEOHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}







	public void setOnScrollStopListner ( IGEOHorizontalScrollView.onScrollStopedListner listner  ) {
		onScrollStoped = listner;
	}

	public void startScrollerTask(){
		initialPosition = getScrollX();
		stopTask = false;
		IGEOHorizontalScrollView.this.postDelayed(scrollerTask, newChek);

	}

	public void stopScrollerTask(){
		stopTask = true;
	}

}
