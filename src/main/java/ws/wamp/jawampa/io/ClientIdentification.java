/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.io;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class ClientIdentification {
    private final Object client;
    
    public ClientIdentification( Object client ) {
        this.client = client;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.client != null ? this.client.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientIdentification other = (ClientIdentification) obj;
        if ( this.client != other.client ) return false; // deliberate ==
        return true;
    }
}
