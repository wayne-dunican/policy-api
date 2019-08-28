/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy API
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.api.main.startstop;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.junit.Test;
import org.onap.policy.api.main.exception.PolicyApiException;
import org.onap.policy.api.main.exception.PolicyApiRuntimeException;

public class TestApiCommandLineArguments {
    private ApiCommandLineArguments apiCmdArgs = new ApiCommandLineArguments();

    @Test(expected = PolicyApiRuntimeException.class)
    public void testApiCommandLineArgumentsStringArray() {
        String [] args = {"---d"};
        new ApiCommandLineArguments(args);
    }

    @Test
    public void testNonExistentFileValidateReadableFile() {
        apiCmdArgs.setConfigurationFilePath("src/test/resources/filetest/nonexist.json ");
        assertThatThrownBy(
                apiCmdArgs::validate
            )
            .isInstanceOf(PolicyApiException.class)
            .hasMessageContaining("file \"src/test/resources/filetest/nonexist.json \" does not exist");
    }

    @Test
    public void testEmptyFileNameValidateReadableFile() {
        apiCmdArgs.setConfigurationFilePath("");
        assertThatThrownBy(
                 apiCmdArgs::validate
            )
            .isInstanceOf(PolicyApiException.class)
            .hasMessageContaining("policy api configuration file was not specified as an argument");
    }

    @Test
    public void testInvalidUrlValidateReadableFile() {
        apiCmdArgs.setConfigurationFilePath("src/test\\resources/filetest\\n");
        assertThatThrownBy(
                apiCmdArgs::validate
            )
            .isInstanceOf(PolicyApiException.class)
            .hasMessageContaining(
                    "policy api configuration file \"src/test\\resources/filetest\\n\" does not exist");
    }

    @Test
    public void testDirectoryValidateReadableFile() {
        apiCmdArgs.setConfigurationFilePath("src/test/resources/");
        assertThatThrownBy(
                apiCmdArgs::validate
            )
            .isInstanceOf(PolicyApiException.class)
            .hasMessageContaining("file \"src/test/resources/\" is not a normal file");
    }

    @Test
    public void testReadPermissionValidateReadableFile() throws IOException {
        String filepath =  "src/test/resources/unreadablefile.json";
        Set<PosixFilePermission> notReadable = PosixFilePermissions.fromString("-wx-wx-wx");
        FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(notReadable);
        Path pathObj = Paths.get(filepath);
        Files.createFile(pathObj, permissions);
        apiCmdArgs.setConfigurationFilePath(filepath);
        assertThatThrownBy(
                apiCmdArgs::validate
            )
            .isInstanceOf(PolicyApiException.class)
            .hasMessageContaining(
                "file \"src/test/resources/unreadablefile.json\" is ureadable");
        Files.deleteIfExists(pathObj);
    }
}
