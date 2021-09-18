using System;
using System.Net;

namespace Jobotwar.WebApp.Features.Api
{
    public class BadRequestException : Exception
    {
        internal BadRequestException(string uri, HttpStatusCode statusCode, string message)
            : base($"{uri} returned {statusCode} with message: {message}")
        {
            Uri = uri;
            StatusCode = statusCode;
            Message = message;
        }

        public string Uri { get; }
        public HttpStatusCode StatusCode { get; }
        public string Message { get; }
    }
}
