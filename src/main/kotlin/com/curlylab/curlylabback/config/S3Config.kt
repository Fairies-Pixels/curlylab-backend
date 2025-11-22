package com.curlylab.curlylabback.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config {

    @Value("\${yandex.s3.access-key}")
    private lateinit var accessKey: String

    @Value("\${yandex.s3.secret-key}")
    private lateinit var secretKey: String

    @Value("\${yandex.s3.endpoint}")
    private lateinit var endpoint: String

    @Value("\${yandex.s3.region}")
    private lateinit var region: String

    @Value("\${yandex.s3.bucket}")
    private lateinit var bucket: String

    @Bean
    fun s3Client(): AmazonS3 {
        val credentials = BasicAWSCredentials(accessKey, secretKey)

        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(endpoint, region)
            )
            .withPathStyleAccessEnabled(true)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }

    @Bean
    fun bucketName(): String = bucket
}