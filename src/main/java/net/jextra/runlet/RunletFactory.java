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
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

/**
 * <p>
 * An application with command-line parameters that are created for you from the
 * {@link ParamFields}s in the getParams() object.
 * </p>
 */
public class RunletFactory
{
    // ============================================================
    // Fields
    // ============================================================

    private Object runlet;
    private Object params;

    // ============================================================
    // Constructors
    // ============================================================

    public RunletFactory( Object runlet, Object params )
    {
        this.runlet = runlet;
        this.params = params;
    }

    // ============================================================
    // Methods
    // ============================================================

    // ----------
    // public
    // ----------

    /**
     * Call this in your main. It takes care of processing args and executing the app.
     */
    public static void main( Object r, Object p, String[] args )
    {
        Date start = new Date();
        boolean success = subMain( r, p, args );
        Date end = new Date();
        long msec = end.getTime() - start.getTime();
        System.out.printf( "(Total time: %d seconds)\n", msec / 1000 );
        System.exit( success ? 0 : 1 );
    }

    /**
     * Call this in your main. It takes care of processing args and executing the app.
     * <p>
     * This method doesn't contain a system.out statement. noPrint added simply to change method signature.
     */
    public static void main( Object r, Object p, String[] args, Boolean noPrint )
    {
        boolean success = subMain( r, p, args );
        System.exit( success ? 0 : 1 );
    }

    private static boolean subMain( Object r, Object p, String[] args )
    {
        boolean success = false;
        try
        {
            RunletFactory factory = new RunletFactory( r, p );
            if ( factory.processArgs( args ) )
            {
                factory.execute();
                success = true;
            }
        }
        catch ( Exception ex )
        {
            success = false;
            ex.printStackTrace();
        }
        return success;
    }

    public static List<String> buildArgs( Class<?> runletClass, Object params )
        throws Exception
    {
        ArrayList<String> args = new ArrayList<>();

        args.add( getScriptName( runletClass ) );

        ParamFields fields = new ParamFields( params );
        for ( ParamField ann : fields.getParamFields() )
        {
            Field field = fields.getField( ann );
            field.setAccessible( true );
            if ( field.get( params ) != null )
            {
                String string = String.format( "--%s=%s", ann.value(), field.get( params ) );
                args.add( string );
            }
        }

        return args;
    }

    public boolean processArgs( String[] args )
        throws Exception
    {
        Pattern promptPattern = Pattern.compile( "--prompt" );
        Pattern helpPattern = Pattern.compile( "(--help|-h|-\\?)" );
        Pattern paramPattern = Pattern.compile( "--([^=]*)=(.*)" );

        Map<String, String> paramsMap = new HashMap<>();
        boolean help = false;
        boolean prompt = false;

        for ( String arg : args )
        {
            if ( arg == null || arg.trim().isEmpty() )
            {
                continue;
            }

            Matcher m = promptPattern.matcher( arg );
            if ( m.matches() )
            {
                prompt = true;
                continue;
            }

            m = helpPattern.matcher( arg );
            if ( m.matches() )
            {
                help = true;
                continue;
            }

            m = paramPattern.matcher( arg );
            if ( m.matches() )
            {
                paramsMap.put( m.group( 1 ), m.group( 2 ) );
                continue;
            }

            System.out.println( "\nUnknown argument: " + arg );
            help = true;
        }

        //
        // Print usage and exit if asked for help.
        //
        if ( help )
        {
            printUsage( paramsMap );
            return false;
        }

        //
        // Check for required parameters.
        //
        ParamFields fields = new ParamFields( params );
        if ( !prompt )
        {
            for ( ParamField ann : fields.getParamFields() )
            {
                if ( !ann.hidden() && ann.required() && !paramsMap.containsKey( ann.value() ) )
                {
                    System.err.printf( "\nMissing required parameter [%s].\n", ann.value() );
                    printUsage( paramsMap );
                    return false;
                }
            }
        }
        else
        {
            prompt( paramsMap );
        }

        //
        // Set field values in params.
        //
        fields.setValues( paramsMap );
        return true;
    }

    protected Method findCommand()
        throws Exception
    {
        Set<Method> commandMethods = new HashSet<>();

        //
        // Search for methods with RunletCommand annotations continuing up the object hierarchy.
        //
        for ( Class<?> cls = runlet.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass() )
        {
            for ( Method method : cls.getDeclaredMethods() )
            {
                if ( method.isAnnotationPresent( RunletCommand.class ) )
                {
                    commandMethods.add( method );
                }
            }
        }

        //
        // Next search for the default "execute" methods up the object hierarchy.
        //
        for ( Class<?> cls = runlet.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass() )
        {
            try
            {
                commandMethods.add( cls.getDeclaredMethod( "execute", new Class<?>[0] ) );
            }
            catch ( NoSuchMethodException ex )
            {
                continue;
            }

            break;
        }

        // ensure we found at least one command method
        if ( commandMethods.isEmpty() )
        {
            throw new RuntimeException(
                String.format( "No method in class [%s] has a @RunletCommand annotation.", runlet.getClass().getSimpleName() ) );
        }

        // if multiple command methods were found, report error
        if ( commandMethods.size() > 1 )
        {
            StringBuilder message = new StringBuilder( "The following command methods were found on runlet " + runlet.getClass().getSimpleName() );
            for ( Method commandMethod : commandMethods )
            {
                message.append( "\n  " ).append( commandMethod );
            }
            message.append( "\nOnly a single command method is allowed" );
            throw new Exception( message.toString() );
        }

        return commandMethods.iterator().next();
    }

    public void execute()
        throws Exception
    {
        Method commandMethod = findCommand();
        commandMethod.setAccessible( true );
        commandMethod.invoke( runlet );
    }

    // ----------
    // private
    // ----------

    private void prompt( Map<String, String> paramsMap )
        throws Exception
    {
        BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

        ParamFields fields = new ParamFields( params );
        for ( ParamField param : fields.getParamFields() )
        {
            if ( !paramsMap.containsKey( param.value() ) )
            {
                System.out.print( param.value() + ": " );
                String string = in.readLine();
                paramsMap.put( param.value(), string );
            }
        }
    }

    private void printUsage( Map<String, String> paramsMap )
        throws Exception
    {

        String scriptName = getScriptName( runlet.getClass() );

        StringBuilder message = new StringBuilder();
        message.append( "\nUsage:\n" );
        message.append( String.format( "    %s [--prompt]", scriptName ) );
        int maxArgLength = 12;

        ParamFields fields = new ParamFields( params );
        for ( ParamField param : fields.getParamFields() )
        {
            if ( param.hidden() )
            {
                continue;
            }

            message.append( " " );
            if ( !param.required() )
            {
                message.append( "[" );
            }
            message.append( String.format( "%s=%s", "--" + param.value(), param.value() ) );
            if ( !param.required() )
            {
                message.append( "]" );
            }

            String value = param.value();
            if ( param.required() )
            {
                value = value + " *";
            }
            maxArgLength = Math.max( maxArgLength, value.length() );
        }
        maxArgLength += 2;
        message.append( String.format( "\n    %s --help|-h|-? \n", scriptName ) );

        for ( ParamField param : fields.getParamFields() )
        {
            if ( param.hidden() )
            {
                continue;
            }

            String desc = param.description();
            if ( desc == null || desc.isEmpty() )
            {
                desc = param.value();
            }

            String value = param.value();
            if ( !param.required() )
            {
                value = "[" + value + "]";
            }

            message.append( String.format( "\n    %-" + maxArgLength + "s    %s", "--" + value, desc ) );

            // include given values if any were provided
            String paramValue = paramsMap.get( value );
            if ( paramValue != null )
            {
                message.append( String.format( "    [value='%s']", paramValue ) );
            }
        }

        message
            .append( String.format( "\n\n    %-" + maxArgLength + "s    %s", "--prompt", "Prompt user for unspecified parameters on command line" ) );
        message.append( String.format( "\n    %-" + maxArgLength + "s    %s\n", "--help, -h, -?", "Display this help" ) );

        System.out.println( message.toString() );
    }

    /**
     * The name of the script for the command-line call. e.g. monkey.sh.
     */
    private static String getScriptName( Class<?> runletClass )
        throws Exception
    {
        String runletName = System.getProperty( "runletName", runletClass.getSimpleName() ).replace( "\\", "/" );
        int slash = runletName.lastIndexOf( "/" );
        return runletName.substring( slash + 1 );
    }
}
