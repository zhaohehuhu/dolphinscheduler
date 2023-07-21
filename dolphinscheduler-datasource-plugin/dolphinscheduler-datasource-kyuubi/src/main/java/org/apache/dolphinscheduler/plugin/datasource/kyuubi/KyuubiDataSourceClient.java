/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.datasource.kyuubi;

import org.apache.dolphinscheduler.plugin.datasource.api.client.CommonDataSourceClient;
import org.apache.dolphinscheduler.plugin.datasource.api.utils.DataSourceUtils;
import org.apache.dolphinscheduler.plugin.datasource.api.utils.PasswordUtils;
import org.apache.dolphinscheduler.spi.datasource.BaseConnectionParam;
import org.apache.dolphinscheduler.spi.enums.DbType;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class KyuubiDataSourceClient extends CommonDataSourceClient {

    private DriverManagerDataSource driverManagerDataSource;
    private Connection connection = null;

    public KyuubiDataSourceClient(BaseConnectionParam baseConnectionParam, DbType dbType) {
        super(baseConnectionParam, dbType);
    }

    @Override
    protected void preInit() {
        log.info("PreInit in {}", getClass().getName());
    }

    @Override
    protected void initClient(BaseConnectionParam baseConnectionParam, DbType dbType) {

        this.driverManagerDataSource =
                new DriverManagerDataSource(DataSourceUtils.getJdbcUrl(DbType.KYUUBI, baseConnectionParam),
                        baseConnectionParam.getUser(), PasswordUtils.decodePassword(baseConnectionParam.getPassword()));
        driverManagerDataSource.setDriverClassName(baseConnectionParam.getDriverClassName());
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        log.info("Init {} {} success.", getClass().getName(),
                DataSourceUtils.getJdbcUrl(DbType.KYUUBI, baseConnectionParam));
    }

    @Override
    protected void checkEnv(BaseConnectionParam baseConnectionParam) {
        super.checkEnv(baseConnectionParam);
    }

    @Override
    public Connection getConnection() {
            try {
                connection = driverManagerDataSource.getConnection();
            } catch (SQLException e) {
                log.error("Failed to get Kyuubi Connection.", e);
            }
        return connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("failed to close the kyuubi connection: {}", e.getMessage(), e);
        }
        log.info("Closed Kyuubi datasource client.");
    }
}
