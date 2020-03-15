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

import java.lang.reflect.*;
import java.util.*;

/**
 * <p>
 * Given a parameters object, extract all fields with {@link ParamField} annotation.
 * </p>
 */
public class ParamFields
{
    // ============================================================
    // Fields
    // ============================================================

    private Object params;
    private Map<ParamField, Field> anns;

    // ============================================================
    // Constructors
    // ============================================================

    public ParamFields( Object params )
    {
        this.params = params;
        anns = new LinkedHashMap<ParamField, Field>();

        if ( params != null )
        {
            Set<String> paramFieldNames = new HashSet<>();
            // walk up params object hierarchy looking for ParamFields to process
            for ( Class<?> cls = params.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass() )
            {
                for ( Field field : cls.getDeclaredFields() )
                {
                    if ( field.isAnnotationPresent( ParamField.class ) )
                    {
                        ParamField ann = field.getAnnotation( ParamField.class );
                        // Ensure unique param field names
                        String fieldName = ann.value();
                        if ( !paramFieldNames.add( fieldName ) )
                        {
                            throw new IllegalArgumentException( String
                                .format( "Duplicate ParamField fieldName '%s' found on '%s' class hierarchy", fieldName,
                                    params.getClass().getName() ) );
                        }
                        anns.put( ann, field );
                    }
                }
            }
        }
    }

    // ============================================================
    // Methods
    // ============================================================

    // ----------
    // public
    // ----------

    public Set<ParamField> getParamFields()
    {
        return anns.keySet();
    }

    public Field getField( ParamField ann )
    {
        return anns.get( ann );
    }

    public void setValues( Map<String, String> values )
    {
        for ( ParamField ann : anns.keySet() )
        {
            String paramFieldName = ann.value();
            if ( values.containsKey( paramFieldName ) )
            {
                setFieldValue( anns.get( ann ), paramFieldName, values.get( paramFieldName ) );
            }
        }
    }

    private void setFieldValue( Field field, String paramFieldName, String paramValue )
    {
        //System.out.printf( "Setting value: %s=%s.\n", paramFieldName, paramValue );
        field.setAccessible( true );

        Class fldType = field.getType();

        try
        {
            field.set( params, StringCoercer.fromString( paramValue, fldType ) );
        }
        catch ( Exception ex )
        {
            throw new IllegalArgumentException( String
                .format( "For ParamField \"%s\", cannot coerce String value(%s) to type:%s", paramFieldName, paramValue, fldType.getSimpleName() ),
                ex );
        }
    }

    public List<String> appendArgs( String... prefix )
        throws Exception
    {
        return appendArgs( Arrays.asList( prefix ) );
    }

    /**
     * Append fields to the given prefix of Strings.
     */
    public List<String> appendArgs( List<String> prefix )
        throws Exception
    {
        ArrayList<String> args = new ArrayList<>( prefix );

        ParamFields fields = new ParamFields( params );
        for ( ParamField ann : getParamFields() )
        {
            Field field = fields.getField( ann );
            field.setAccessible( true );
            Object value = field.get( params );

            if ( value == null )
            {
                continue;
            }

            args.add( String.format( "--%s=\"%s\"", ann.value(), StringCoercer.toString( value ) ) );
        }
        return args;
    }
}
