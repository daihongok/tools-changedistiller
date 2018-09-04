package ch.uzh.ifi.seal.changedistiller.main;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class WhenExamplesFromRealProjectsAreDistilled {

    private static final String TEST_DATA = "src_change/";
    private static FileDistiller distiller;

    @BeforeClass
    public static void initialize() {
        distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
    }

    @Test
    public void noStackOverflowErrorShouldOccur() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (StackOverflowError err) {
        	fail("Source code change extraction failed because of a stack overflow (most likely while doing a post-order traversal of T1 during the edit script generation).");
        }

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if(changes != null) {
			for(SourceCodeChange change : changes) {
				// see Javadocs for more information
				String changeType = change.getChangeType().toString();
				String changedEntityType = change.getChangedEntity().getType().toString();

				if (changeType.equals("STATEMENT_DELETE") || changeType.equals("STATEMENT_INSERT")
						|| changeType.equals("STATEMENT_ORDERING_CHANGE")
						|| changeType.equals("STATEMENT_PARENT_CHANGE") || changeType.equals("STATEMENT_UPDATE")) {
					changeType = changedEntityType + changeType.substring(9);
					// System.out.println(changeType);
				}

				if (changeType.equals("CONDITION_EXPRESSION_CHANGE")) {
					changeType = changedEntityType + "_" + changeType;
					// System.out.println(changeType);
				}

				System.out.println(changeType);
				System.out.println(changedEntityType);
			}
		}
    }
    

}
