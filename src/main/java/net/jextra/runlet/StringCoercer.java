/*
 * Copyright (C) jextra.net.
 *
 * This file is part of the jextra.net software.
 *
 * The jextra software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * The jextra software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with the jextra software; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA.
 */

package net.jextra.runlet;

import java.io.*;
import java.text.*;
import java.util.*;

public class StringCoercer
{
    // ============================================================
    // Methods
    // ============================================================

    // ----------
    // public
    // ----------

    public static String toString( Object obj )
    {
        if ( obj == null )
        {
            return null;
        }
        else if ( obj instanceof String )
        {
            return (String) obj;
        }
        else
        {
            return obj.toString();
        }
    }

    public static Object fromString( String string, Class<?> destClass )
        throws ParseException
    {
        if ( string == null )
        {
            return null;
        }
        else if ( destClass.equals( String.class ) )
        {
            return string;
        }
        else if ( destClass.equals( Boolean.TYPE ) || destClass.equals( Boolean.class ) )
        {
            return Boolean.parseBoolean( string );
        }
        else if ( destClass.equals( Byte.TYPE ) || destClass.equals( Byte.class ) )
        {
            return Byte.parseByte( string );
        }
        else if ( destClass.equals( Short.TYPE ) || destClass.equals( Short.class ) )
        {
            return Short.parseShort( string );
        }
        else if ( destClass.equals( Integer.TYPE ) || destClass.equals( Integer.class ) )
        {
            return Integer.parseInt( string );
        }
        else if ( destClass.equals( Long.TYPE ) || destClass.equals( Long.class ) )
        {
            return Long.parseLong( string );
        }
        else if ( destClass.equals( Float.TYPE ) || destClass.equals( Float.class ) )
        {
            return Float.parseFloat( string );
        }
        else if ( destClass.equals( Double.TYPE ) || destClass.equals( Double.class ) )
        {
            return Double.parseDouble( string );
        }
        else if ( destClass.equals( Date.class ) )
        {
            DateFormat df = DateFormat.getDateInstance();
            return df.parse( string );
        }
        else if ( destClass.equals( File.class ) )
        {
            return new File( string );
        }
        else if ( destClass.equals( UUID.class ) )
        {
            return UUID.fromString( string );
        }

        throw new RuntimeException( "Unable to convert string '" + string + "' to " + destClass.getCanonicalName() + "." );
    }
}
