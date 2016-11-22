package io.fourfinanceit.push.test.load.swrve.mock.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MessageDataRepository extends JpaRepository<MessageData, Long> {
    
    @Query(value = """
        select 
            count(md) 
        from
            MessageData md 
        where 
            md.threadCode = :threadCode 
            and md.messageCode = :messageCode
    """)
    int count(
            @Param("threadCode") int threadCode, 
            @Param("messageCode") int messageCode
    )
}
