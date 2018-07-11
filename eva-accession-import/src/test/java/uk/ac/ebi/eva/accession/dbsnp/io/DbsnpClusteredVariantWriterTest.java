/*
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
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
 */
package uk.ac.ebi.eva.accession.dbsnp.io;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ampt2d.commons.accession.hashing.SHA1HashingFunction;

import uk.ac.ebi.eva.accession.core.IClusteredVariant;
import uk.ac.ebi.eva.accession.core.ClusteredVariant;
import uk.ac.ebi.eva.accession.core.configuration.MongoConfiguration;
import uk.ac.ebi.eva.accession.core.summary.DbsnpClusteredVariantSummaryFunction;
import uk.ac.ebi.eva.accession.dbsnp.persistence.DbsnpClusteredVariantEntity;
import uk.ac.ebi.eva.accession.dbsnp.test.MongoTestConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class, MongoTestConfiguration.class})
public class DbsnpClusteredVariantWriterTest {
    private static final int TAXONOMY_1 = 3880;

    private static final int TAXONOMY_2 = 3882;

    private static final long EXPECTED_ACCESSION = 10000000000L;

    private static final long EXPECTED_ACCESSION_2 = 10000000001L;

    private static final String CONTIG_1 = "contig_1";

    private static final String CONTIG_2 = "contig_2";

    private static final int START_1 = 100;

    private static final int START_2 = 200;

    private static final String ALTERNATE_ALLELE = "T";

    private static final String REFERENCE_ALLELE = "A";

    private static final int ACCESSION_COLUMN = 2;

    private static final String ACCESSION_PREFIX = "ss";

    private static final Long CLUSTERED_VARIANT = null;

    private static final Boolean SUPPORTED_BY_EVIDENCE = null;

    private static final Boolean MATCHES_ASSEMBLY = null;

    private static final Boolean ALLELES_MATCH = null;

    private static final Boolean VALIDATED = null;

    private DbsnpClusteredVariantWriter dbsnpClusteredVariantWriter;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Function<IClusteredVariant, String> hashingFunction;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        dbsnpClusteredVariantWriter = new DbsnpClusteredVariantWriter(mongoTemplate);
        hashingFunction = new DbsnpClusteredVariantSummaryFunction().andThen(new SHA1HashingFunction());
        mongoTemplate.dropCollection(DbsnpClusteredVariantEntity.class);
    }

    @Test
    public void saveSingleAccession() throws Exception {
        ClusteredVariant clusteredVariant = new ClusteredVariant("assembly", TAXONOMY_1, "contig", START_1,
                                                                 "reference", "alternate", 
                                                                   VALIDATED);
        DbsnpClusteredVariantEntity variant = new DbsnpClusteredVariantEntity(EXPECTED_ACCESSION,
                                                                              hashingFunction.apply(clusteredVariant),
                                                                              clusteredVariant);

        dbsnpClusteredVariantWriter.write(Collections.singletonList(variant));

        List<DbsnpClusteredVariantEntity> accessions = mongoTemplate.find(new Query(),
                                                                          DbsnpClusteredVariantEntity.class);
        assertEquals(1, accessions.size());
        assertEquals(EXPECTED_ACCESSION, (long) accessions.get(0).getAccession());

        assertEquals(clusteredVariant, new ClusteredVariant(accessions.get(0)));
    }

    @Test
    public void saveDifferentTaxonomies() throws Exception {
        ClusteredVariant firstClusteredVariant = new ClusteredVariant("assembly", TAXONOMY_1, "contig",
                                                                      START_1, "reference", "alternate",
                                                                      CLUSTERED_VARIANT, 
                                                                        VALIDATED);
        ClusteredVariant secondClusteredVariant = new ClusteredVariant("assembly", TAXONOMY_2, "contig",
                                                                       START_1, "reference", "alternate",
                                                                       CLUSTERED_VARIANT, 
                                                                         VALIDATED);
        DbsnpClusteredVariantEntity firstVariant = new DbsnpClusteredVariantEntity(
                EXPECTED_ACCESSION, hashingFunction.apply(firstClusteredVariant), firstClusteredVariant);
        DbsnpClusteredVariantEntity secondVariant = new DbsnpClusteredVariantEntity(
                EXPECTED_ACCESSION_2, hashingFunction.apply(secondClusteredVariant), secondClusteredVariant);

        dbsnpClusteredVariantWriter.write(Arrays.asList(firstVariant, secondVariant));

        List<DbsnpClusteredVariantEntity> accessions = mongoTemplate.find(new Query(),
                                                                          DbsnpClusteredVariantEntity.class);
        assertEquals(2, accessions.size());
        assertEquals(EXPECTED_ACCESSION, (long) accessions.get(0).getAccession());
        assertEquals(EXPECTED_ACCESSION_2, (long) accessions.get(1).getAccession());

        assertEquals(firstClusteredVariant, new ClusteredVariant(accessions.get(0)));
        assertEquals(secondClusteredVariant, new ClusteredVariant(accessions.get(1)));
    }

    @Test
    public void failsOnDuplicateVariant() throws Exception {
        ClusteredVariant clusteredVariant = new ClusteredVariant("assembly", TAXONOMY_1, "contig",
                                                                 START_1, "reference", "alternate",
                                                                 CLUSTERED_VARIANT, 
                                                                   VALIDATED);
        DbsnpClusteredVariantEntity variant = new DbsnpClusteredVariantEntity(
                EXPECTED_ACCESSION, hashingFunction.apply(clusteredVariant), clusteredVariant);

        thrown.expect(RuntimeException.class);
        dbsnpClusteredVariantWriter.write(Arrays.asList(variant, variant));
    }

}
