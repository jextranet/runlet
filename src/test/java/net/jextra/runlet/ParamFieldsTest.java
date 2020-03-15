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

import java.util.*;
import org.junit.*;

/**
 * Created by pedrozaf on 6/11/15.
 */
public class ParamFieldsTest
{
    @Test( expected = IllegalArgumentException.class )
    public void test_setValues_type_coersion_error()
        throws Exception
    {

        TestParams params = new TestParams( "foo", 5 );

        ParamFields paramFields = new ParamFields( params );

        HashMap<String, String> values = new HashMap<>();
        values.put( "int param", "nan" );
        paramFields.setValues( values );
    }

    @Test( expected = IllegalArgumentException.class )
    public void test_constructor_duplicateFieldName()
        throws Exception
    {
        new ParamFields( new TestDuplicateFieldNameParams() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void test_constructor_duplicateFieldName_in_hierarchy()
        throws Exception
    {
        Assert.assertNotNull( new ParamFields( new TestParamsBase() ) ); // this should work fine since no duplicates exist

        new ParamFields( new TestParamsSubclass() ); // this should fail since there is a duplicated field name
    }

    class TestParams
    {
        @ParamField( "string param" )
        private String aString;

        @ParamField( "int param" )
        private int anInt;

        TestParams( String aString, int anInt )
        {
            this.aString = aString;
            this.anInt = anInt;
        }
    }

    class TestDuplicateFieldNameParams
    {
        @ParamField( value = "fieldName", required = false )
        private String foo;

        @ParamField( value = "fieldName", required = false ) // duplicate
        private String bar;
    }

    class TestParamsBase
    {
        @ParamField( value = "fieldName", required = false )
        private String foo;
    }

    class TestParamsSubclass extends TestParamsBase
    {
        @ParamField( value = "fieldName", required = false ) // duplicate
        private String bar;
    }

}
