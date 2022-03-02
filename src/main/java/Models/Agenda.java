package Models;

import java.time.LocalDateTime;

public class Agenda extends AbstractEntity {
    private Paciente paciente;
    private Medico medico;
    private StatusAgenda statusAgenda;
    private LocalDateTime dataAgendamento;
    private Boolean encaixe;

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public StatusAgenda getStatusAgenda() {
        return statusAgenda;
    }

    public void setStatusAgenda(StatusAgenda statusAgenda) {
        this.statusAgenda = statusAgenda;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public Boolean getEncaixe() {
        return encaixe;
    }

    public void setEncaixe(Boolean encaixe) {
        this.encaixe = encaixe;
    }
}
