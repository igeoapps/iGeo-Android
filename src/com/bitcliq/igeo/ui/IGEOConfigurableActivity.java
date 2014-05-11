package com.bitcliq.igeo.ui;


/**
 * Esta interface define um tipo a ser usado por Activity's que obtêm e aplicam uma série de configurações,
 * pelo que, devem implementar o método applyConfigs. Estas configurações, tratam-se das configurações dos ecrãs lidas de um ficheiro ou servidor
 * @author Bitcliq, Lda.
 *
 */
public interface IGEOConfigurableActivity 
{
	
	/**
	 * Obtémm do IGEOConfigsManager as configurações do ecrã atual, aplica-as, e guarda-as na variável screenConfigs.
	 * Poderão existir configurações que não são aqui obtidas, mas sim durante o ciclo de vida da Activity, por exemplo,
	 * quando se tratam de ícones a ser colocados em listas, que só são construidas após o inicio da atividade, entre outros
	 * casos aplicáveis.
	 */
	public void applyConfigs();
	
	
}

