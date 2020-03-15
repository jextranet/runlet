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

public class StringCoercerTest
{
    @Test
    public void test_fromString_uuid()
        throws Exception
    {
        String uuidValue = "79a0c45a-954f-4dcf-a2f5-1ed7a6fa8f8f";
        UUID value = (UUID) StringCoercer.fromString( uuidValue, UUID.class );
        Assert.assertEquals( uuidValue, value.toString() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void test_fromString_uuid_invalid()
        throws Exception
    {
        StringCoercer.fromString( "this is not a uuid", UUID.class );
    }

}
