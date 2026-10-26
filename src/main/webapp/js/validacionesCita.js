// Validaciones del formulario de cita antes de enviarlo (la validacion real esta en CitaValidator y CitaDAO).
document.getElementById('formCita').addEventListener('submit', function (evento) {
    const paciente = document.getElementById('idPaciente').value;
    const medico = document.getElementById('idMedico').value;
    const fecha = document.getElementById('fecha').value;
    const hora = document.getElementById('hora').value;
    const estado = document.getElementById('estado').value;
    let error = '';

    const hoy = new Date();
    const hoyTexto = [hoy.getFullYear(), String(hoy.getMonth() + 1).padStart(2, '0'), String(hoy.getDate()).padStart(2, '0')].join('-');
    const agendada = estado === 'PENDIENTE' || estado === 'CONFIRMADA';

    if (!paciente) error = 'Selecciona un paciente.';
    else if (!medico) error = 'Selecciona un médico.';
    else if (!fecha) error = 'La fecha es obligatoria.';
    else if (agendada && fecha < hoyTexto) error = 'No se puede agendar una cita en una fecha pasada.';
    else if (!hora) error = 'La hora es obligatoria.';
    else {
        const partes = hora.split(':');
        const minutos = parseInt(partes[0], 10) * 60 + parseInt(partes[1], 10);
        if (minutos < 7 * 60 || minutos > 18 * 60) error = 'El horario de atención es de 07:00 a 18:00.';
        else if (parseInt(partes[1], 10) % 30 !== 0) error = 'Las citas se agendan cada 30 minutos (ej. 08:00, 08:30).';
    }

    if (error) {
        evento.preventDefault();
        alert(error);
    }
});
