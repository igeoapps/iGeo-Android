package com.bitcliq.igeo.core;

import android.os.CountDownTimer;

/**
 * Esta classe é utilizada para efetuar ações que duram um determinado intervalo de tempo, as quais
 * são compostas por vários passos. Ao percorrer cada um desses passos é possível
 * definir ações que queremos efetuar, bem como quando todos os passos são concluídos.
 * Nesta App são utilizados objetos deste tipo no efeito de deslocação do ecrã da home para ambos os lados e na
 * apresentação e ocultação da legenda do mapa. Outros efeitos podem ser usados.
 * @author Bitcliq, Lda.
 *
 */
public class IGEOCountDownTimer extends CountDownTimer {
	
	public boolean canceled = false;

	public IGEOCountDownTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		
	}

}