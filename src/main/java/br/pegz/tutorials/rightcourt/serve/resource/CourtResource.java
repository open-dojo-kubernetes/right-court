package br.pegz.tutorials.rightcourt.serve.resource;

import br.pegz.tutorials.rightcourt.persistence.Play;
import br.pegz.tutorials.rightcourt.serve.exception.PointException;

public interface CourtResource {

    /**
     * Sends the play to the other side of the court
     *
     * @param myPlay to send across the court.
     * @return received play
     * @throws PointException if there is an point in the received play or some error occurs.
     */
    Play sendPlayToOtherSide(Play myPlay) throws PointException;
}
