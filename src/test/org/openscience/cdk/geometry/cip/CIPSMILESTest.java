/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.geometry.cip;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.cip.CIPTool.CIP_CHIRALITY;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-cip
 */
public class CIPSMILESTest extends CDKTestCase {

    static SmilesParser smiles = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());

    @Test
    public void test() throws Exception {
        IMolecule molecule = smiles.parseSmiles("ClC(Br)(I)[H]");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 1, 4, 0, 2, 3, Stereo.CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    /**
     * Test case that tests sequence recursing of the atomic number rule. 
     *
     * @cdk.inchi InChI=1S/C5H12O/c1-3-5(2)4-6/h5-6H,3-4H2,1-2H3/t5-/m1/s1
     *
     * @see #test2methylbutanol_S()
     */
    @Test
    public void test2methylbutanol_R() throws Exception {
        IMolecule molecule = smiles.parseSmiles("OCC([H])(C)CC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 2, 3, 1, 4, 5, Stereo.CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    /**
     * Test case that tests sequence recursing of the atomic number rule. 
     *
     * @cdk.inchi InChI=1S/C5H12O/c1-3-5(2)4-6/h5-6H,3-4H2,1-2H3/t5-/m0/s1
     *
     * @see #test2methylbutanol_R()
     */
    @Test
    public void test2methylbutanol_S() throws Exception {
        IMolecule molecule = smiles.parseSmiles("OCC([H])(C)CC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 2, 3, 1, 4, 5, Stereo.ANTI_CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testTwoVersusDoubleBondedOxygen_R() throws Exception {
        IMolecule molecule = smiles.parseSmiles("OC(O)C([H])(C)C=O");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 3, 4, 5, 1, 6, Stereo.CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testTwoVersusDoubleBondedOxygen_S() throws Exception {
        IMolecule molecule = smiles.parseSmiles("OC(O)C([H])(C)C=O");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 3, 4, 5, 1, 6, Stereo.ANTI_CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testImplicitHydrogen() throws Exception {
        IMolecule molecule = smiles.parseSmiles("CCC(C)CCC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 2, CIPTool.HYDROGEN, 3, 1, 4, Stereo.ANTI_CLOCKWISE
        );
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test(timeout=5000) // 5 seconds should be enough
    public void testTermination() throws Exception {
        IMolecule mol = smiles.parseSmiles("[H]O[C@]([H])(C1([H])(C([H])([H])C([H])([H])C1([H])([H])))C2([H])(C([H])([H])C2([H])([H]))");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        CIPTool.getCIPChirality(mol, (ITetrahedralChirality)stereo);
    }
}


