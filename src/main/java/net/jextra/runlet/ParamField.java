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

import java.lang.annotation.*;

/**
 * <p>
 * Add this to anything in a runlet params object in order for it to be available on command-line.
 * </ul>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ParamField
{
    // Field name
    String value();

    String description() default "";

    boolean required() default true;

    boolean hidden() default false;
}
