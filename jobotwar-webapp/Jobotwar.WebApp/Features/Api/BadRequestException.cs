using System;
using System.Net;

namespace Jobotwar.WebApp.Features.Api
{
    public class BadRequestException : Exception
    {
        internal BadRequestException(string uri, HttpStatusCode statusCode, string remoteMessage)
            : base($"{uri} returned {statusCode} with message: {remoteMessage}")
        {
            Uri = uri;
            StatusCode = statusCode;
            RemoteMessage = remoteMessage;
        }

        public string Uri { get; }
        public HttpStatusCode StatusCode { get; }
        public string RemoteMessage { get; }
    }
}
