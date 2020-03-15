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

package net.jextra.runlet.test;

import net.jextra.runlet.*;

public class TestRunlet
{
    // ============================================================
    // Fields
    // ============================================================

    private Params params;

    // ============================================================
    // Constructors
    // ============================================================

    public TestRunlet()
    {
        params = new Params();
    }

    // ============================================================
    // Methods
    // ============================================================

    // ----------
    // public
    // ----------

    public static void main( String[] args )
    {
        TestRunlet runlet = new TestRunlet();
        RunletFactory.main( runlet, runlet.params, args );
    }

    @RunletCommand
    public void run()
    {
        System.out.printf( "Hello %s,\n", params.getName() );
        System.out.printf( "According to our records you are %d years old.\n", params.getAge() );
    }

    // ============================================================
    // Inner Classes
    // ============================================================

    public static class Params
    {
        @ParamField( value = "name", description = "Example name", required = true )
        public String name;

        @ParamField( value = "age", description = "Example age", required = true )
        public int age;

        @ParamField( value = "ssn", description = "Optional SSN", required = false )
        public String ssn;

        @ParamField( value = "secret", description = "Hidden secret", hidden = true )
        public String secret;

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public int getAge()
        {
            return age;
        }

        public void setAge( int age )
        {
            this.age = age;
        }

        public String getSecret()
        {
            return secret;
        }

        public void setSecret( String secret )
        {
            this.secret = secret;
        }

        public String getSsn()
        {
            return ssn;
        }

        public void setSsn( String ssn )
        {
            this.ssn = ssn;
        }
    }
}
