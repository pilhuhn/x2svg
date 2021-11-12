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
 * The content model of an elements children
 *
 * @author hwr@pilhuhn.de
 * @since 1.0
 */
public enum ContentModel {

    NONE, // ???
    /**
     * We can have mixed content (elements and text)
     */
    MIXED,
    /**
     * A sequence of elements
     */
    SEQUENCE,
    /**
     * A choice of elements
     */
    CHOICE,
    /**
     * Displayed children inherit from the parent on the left side
     */
    INHERITANCE,
    /**
     * The children are substituted by the stuff on the right
     */
    SUBSTITUTION
}
