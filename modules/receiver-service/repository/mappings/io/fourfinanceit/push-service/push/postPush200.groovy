io.codearte.accurest.dsl.GroovyDsl.make {
    request {
        url '/push'
        method 'GET'
        method 'POST'
        body([
                platform           : 'ANDROID',
                messagePrototypeKey: 'Hello',
                userId             : 'user-id',
                deviceId           : 'device-id',
                message            : 'This has an exclamation mark (!), a comma and a question mark (?)',
        ])
        headers {
            header('Content-Type', 'application/vnd.fourfinanceit.v1+json')
        }
    }
    response {
        status 200
    }
}
