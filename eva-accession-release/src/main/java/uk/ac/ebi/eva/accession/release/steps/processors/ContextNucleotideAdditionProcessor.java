/*
 *
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
 *
 */
package uk.ac.ebi.eva.accession.release.steps.processors;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.batch.item.ItemProcessor;

import uk.ac.ebi.eva.accession.dbsnp.io.FastaSynonymSequenceReader;
import uk.ac.ebi.eva.commons.core.models.IVariant;
import uk.ac.ebi.eva.commons.core.models.pipeline.Variant;

import static java.lang.Math.max;

public class ContextNucleotideAdditionProcessor implements ItemProcessor<Variant, IVariant> {

    private FastaSynonymSequenceReader fastaSequenceReader;

    public ContextNucleotideAdditionProcessor(FastaSynonymSequenceReader fastaReader) {
        this.fastaSequenceReader = fastaReader;
    }

    @Override
    public IVariant process(Variant variant) throws Exception {
        long oldStart = variant.getStart();
        String contig = variant.getChromosome();
        String oldReference = variant.getReference();
        String oldAlternate = variant.getAlternate();

        if (fastaSequenceReader.doesContigExist(contig)) {
            return getVariantWithContextNucleotide(variant, contig, oldStart, oldReference, oldAlternate);
        } else {
            throw new IllegalArgumentException("Contig '" + contig + "' does not appear in the fasta file ");
        }
    }

    private IVariant getVariantWithContextNucleotide(Variant variant, String contig, long oldStart,
                                                     String oldReference, String oldAlternate) {
        String newReference;
        String newAlternate;
        if (oldReference.isEmpty() || oldAlternate.isEmpty()) {
            ImmutableTriple<Long, String, String> contextNucleotideInfo =
                    fastaSequenceReader.getContextNucleotideAndNewStart(contig, oldStart, oldReference,
                                                                        oldAlternate);

            long newStart = contextNucleotideInfo.getLeft();
            newReference = contextNucleotideInfo.getMiddle();
            newAlternate = contextNucleotideInfo.getRight();
            long newEnd = newStart + max(newReference.length(), newAlternate.length()) - 1;
            variant.renormalizeVariant(newStart, newEnd, newReference, newAlternate);
            return variant;
        } else {
            return variant;
        }
    }
}
