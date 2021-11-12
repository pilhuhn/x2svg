/*******************************************************************************
 * Copyright (c) 2007 Heiko W. Rupp. 	All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package de.bsd.x2svg;

/**
 * Cardinalities for elements. This is expressed as [from,to] with from being
 * the lower bound and to the upper. If 'from' is 0, the Cardinality is optional.
 * If the flag 'empty' is set, then the Cardinality is optional, but from
 * is marked as the empty string. This is done to allow for rendering of elements
 * like a non-optional element, while still having an empty 'from value.
 *
 * @author hwr@pilhuhn.de
 */
public class Cardinality {
    /**
     * lower value
     */
    private String from;
    /**
     * upper value
     */
    private String to;
    private boolean empty;

    /**
     * Construct a Cardinality object where to and from are empty.
     * This can be used if X2svg renders structures where giving
     * a Cardinality makes no sense.
     *
     * @param empty true if no cardinality should be used.
     */
    public Cardinality(boolean empty) {
        this.empty = empty;
        from = "";
        to = "";
    }

    /**
     * Construct a Cardinality object with explicitly given from and to
     * as String
     *
     * @param from the lower value of the cardinality pair
     * @param to   upper value of cardinality pair. Infinite should be given as Asterisk ('*')
     */
    public Cardinality(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Construct a Cardinality object with explicitly given from and to
     * Note: infinite must be given as Integer.MAX_VALUE
     *
     * @param from the lower value of the cardinality pair
     * @param to   upper value of cardinality pair. Unbounded is expressed as -1
     */
    public Cardinality(int from, int to) {
        this.from = String.valueOf(from);
        this.to = String.valueOf(to);
        if (to == -1 || to == Integer.MAX_VALUE)
            this.to = "*";
    }

    /**
     * Construct a Cardinality object by means of values from a DTD
     *
     * @param dtdValue Cardinalities as found in DTDs
     */
    public Cardinality(DTDValues dtdValue) {
        switch (dtdValue) {
            case ZERO_OR_MORE:
                from = "0";
                to = "*";
                break;
            case ZERO_OR_ONE:
                from = "0";
                to = "1";
                break;
            case ONE:
                from = "1";
                to = "1";
                break;
            case ONE_OR_MORE:
                from = "1";
                to = "*";
                break;
        }
    }

    /**
     * Cardinalities as found in DTDs
     *
     * @author hwr@pilhuhn.de
     */
    public enum DTDValues {
        /**
         *
         */
        ZERO_OR_MORE,  // *
        /**
         * ?
         */
        ZERO_OR_ONE,   // ?
        /**
         *
         */
        ONE,
        /**
         * +
         */
        ONE_OR_MORE    // +
    }

    /**
     * Is this element optional (from is 0 or empty or null)
     *
     * @return true if the element is an optional element
     */
    public boolean isOptional() {
        if (empty)
            return false; // Not optional, so render solid
        return from == null || from.equals("0");
	}


	/**
	 * @return the lower value of the cardinality pair
	 */
	public String getFrom() {
		return from;
	}

    /**
     * @return the higher value of the cardinality pair
     */
    public String getTo() {
        return to;
    }
}
