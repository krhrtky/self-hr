package com.example.applications.libs

import com.example.applications.config.AWSConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForbiddenException
import software.amazon.awssdk.services.cognitoidentityprovider.model.InternalErrorException
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException
import software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordResetRequiredException
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException
import software.amazon.awssdk.services.cognitoidentityprovider.model.TooManyRequestsException
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException
import java.net.URI

@Component
class Authenticator(
    private val awsConfig: AWSConfig,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    private val client = CognitoIdentityProviderClient.builder()
        .region(Region.of(awsConfig.region))
        .apply {
            awsConfig.overrideUrl
                ?.let(::URI)
                ?.let(::endpointOverride)
        }
        .build()

    fun getUserInfoFrom(token: String): UserIdentification? {
        return runCatching {
            val user = client
                .getUser {
                    it.accessToken(token)
                }

            UserIdentification(id = user.username())
        }
            .getOrElse {
                when (it) {
                    is ResourceNotFoundException,
                    is InvalidParameterException,
                    is NotAuthorizedException,
                    is TooManyRequestsException,
                    is PasswordResetRequiredException,
                    is UserNotFoundException,
                    is UserNotConfirmedException,
                    is InternalErrorException,
                    is ForbiddenException,
                    is AwsServiceException,
                    is SdkClientException,
                    is CognitoIdentityProviderException -> {
                        logger.error("Authentication error", it)
                        null
                    }
                    else -> {
                        throw it
                    }
                }
            }
    }
}

data class UserIdentification(
    val id: String
)
