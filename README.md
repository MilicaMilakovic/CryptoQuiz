# CryptoQuiz

Cryptography | University Project 2022

- Symmetric cryptography 
- Asymmetric cryptography
- Steganography
- Digital certificate issuance
- Digital certificate revocation & CRLs
- PKI
- Hashing
- Java Keystore API 
- Bouncy Castle library
- JavaFX & JFoeniX

<hr> 
<p align="center" > 
  <img src="./demo.gif" width=700px >
</p>

In order to participate in the quiz, a player needs to register. Upon registration, a random CA entity will issue a digital certificate, which then will be used to login.
A user can login three times. After that, digital certificate will be revoked, due to the cessation of operation.

CA infrastructure is realized using OpenSSL and its hierarchy consists of two levels:

      1. level is ROOTCA, which only signs two subordinate CAs
      2. level is represented by two CA entities which randomly issue end-user certificates. 
         Each CA entity can revoke certificate they signed, and both of them generate a CRL.

The quiz question database consists of 20 questions, both select and input type. The questions are encrypted, encoded and hidden inside random pictures, and thus can be read 
only through the application.

The file with results is also encrypted, while user's certificate and private key are secured by storing in the keystore.

<hr>
