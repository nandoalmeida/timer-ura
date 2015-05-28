package br.com.vivo;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimerTask;

import jcifs.smb.SmbException;

class Task extends TimerTask {
	private static int[] horasAgendadas;
	private static final int intervaloDeHoras = 1;
	private static final int horaInicialDoDia = 0;

	public Task() {
		preencheVetor();
	}

	public static void preencheVetor() {
		horasAgendadas = new int[24 / intervaloDeHoras];
		horasAgendadas[0] = horaInicialDoDia;
		int i = 1;
		while (i < horasAgendadas.length) {
			if ((horasAgendadas[i - 1] + intervaloDeHoras) < 24) {
				horasAgendadas[i] = horasAgendadas[i - 1] + intervaloDeHoras;
			} else {
				horasAgendadas[i] = (horasAgendadas[i - 1] + intervaloDeHoras) - 24;
			}
			i++;
		}
	}

	public void run() {
		Arquivo.criarArquivoLog();
		Arquivo.log("Início da execução da Task");
		if (horaMarcada()) {
			Arquivo.log("Está na hora marcada.");
			try {
				Arquivo.criarArquivoNaRede();
				if (Arquivo.conexaoDeRede) {
					Relatorio.gerarArquivoLocal();
					if (Conexao.isConnected) {
						Arquivo.copiarDoLocalParaRede();
						if (Arquivo.fileSmb.exists()) {
							Arquivo.log("O arquivo já foi copiado com sucesso para a pasta na rede.");
							//Arquivo.deletarArquivoLocal();
						} else {
							Arquivo.log("Falha no momento de copiar o arquivo para a pasta na rede.");
						}
					}
				}
			} catch (SmbException e) {
				Arquivo.log(e.getMessage());
			} catch (SQLException e) {
				Arquivo.log(e.getMessage());
			} catch (Exception e) {
				Arquivo.log(e.getMessage());
			} finally {
				Arquivo.log("A task finalizou. \r\n \r\n");
			}
		} else {
			Arquivo.log("Não está na hora marcada. \r\n \r\n");
		}
	}

	private boolean horaMarcada() {
		Calendar dataAtual = Calendar.getInstance();
		int horaAtual = dataAtual.get(Calendar.HOUR_OF_DAY);
		for (int i = 0; i < horasAgendadas.length; i++) {
			if (horasAgendadas[i] == horaAtual) {
				return true;
			}
		}
		return false;
	}

}