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

package uk.ac.ebi.eva.accession.release.parameters;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

public class InputParameters {

    private String assemblyAccession;

    private String assemblyName;

    private String fasta;

    private String assemblyReportUrl;

    private String outputVcf;

    private boolean forceRestart;

    private Long buildNumber;

    private int chunkSize;

    private int pageSize;

    public JobParameters toJobParameters() {
        return new JobParametersBuilder()
                .addString("assemblyAccession", assemblyAccession)
                .addString("assemblyName", assemblyName)
                .addString("fasta", fasta)
                .addString("assemblyReportUrl", assemblyReportUrl)
                .addLong("buildNumber", buildNumber)
                .addString("outputVcf", outputVcf)
                .addLong("pageSize", (long) pageSize, false)
                .toJobParameters();
    }

    public String getAssemblyAccession() {
        return assemblyAccession;
    }

    public void setAssemblyAccession(String assemblyAccession) {
        this.assemblyAccession = assemblyAccession;
    }

    public String getAssemblyName() {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    public String getFasta() {
        return fasta;
    }

    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    public String getAssemblyReportUrl() {
        return assemblyReportUrl;
    }

    public void setAssemblyReportUrl(String assemblyReportUrl) {
        this.assemblyReportUrl = assemblyReportUrl;
    }

    public String getOutputVcf() {
        return outputVcf;
    }

    public void setOutputVcf(String outputVcf) {
        this.outputVcf = outputVcf;
    }

    public boolean isForceRestart() {
        return forceRestart;
    }

    public void setForceRestart(boolean forceRestart) {
        this.forceRestart = forceRestart;
    }

    public Long getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(Long buildNumber) {
        this.buildNumber = buildNumber;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
