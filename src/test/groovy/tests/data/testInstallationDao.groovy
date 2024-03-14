package tests.data

import static tests.TestUtils.*

logger.info(db.installationDao.queryForAll().collect{it.host}.join(', '))
