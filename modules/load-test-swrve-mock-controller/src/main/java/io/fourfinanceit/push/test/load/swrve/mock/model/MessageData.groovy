package io.fourfinanceit.push.test.load.swrve.mock.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(indexes = @Index(columnList = "threadCode,messageCode"))
class MessageData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id
    
    String text
    
    int threadCode
    
    int messageCode

    int httpStatusCode

    @Temporal(TemporalType.TIMESTAMP)
    Date time
}
