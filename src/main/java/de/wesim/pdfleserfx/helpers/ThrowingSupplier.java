/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.pdfleserfx.helpers;

import java.io.IOException;

/**
 *
 * @author cwrsi
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
   T get() throws IOException;
}
