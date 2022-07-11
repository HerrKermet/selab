<?php

// Exceptions which map to http response codes
class HttpException extends Exception {
    protected $httpCode = 0;

    public function getHttpCode(): int {
        return $this->httpCode;
    }
}

class BadRequest extends HttpException {
    public function __construct($message = 'Bad request', $code = 0, Throwable $previous = null) {
        $this->httpCode = 400;
        $this->message = $message;
        $this->code = $code;
        $this->previous = $previous;
    }
}

class Unauthorized extends HttpException {
    public function __construct($message = 'Unauthorized', $code = 0, Throwable $previous = null) {
        $this->httpCode = 401;
        $this->message = $message;
        $this->code = $code;
        $this->previous = $previous;
    }
}

?>
