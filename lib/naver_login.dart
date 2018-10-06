import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class NaverLogin {
  @visibleForTesting
  static const MethodChannel channel = const MethodChannel('io.github.kdy1/naver_login');

  static final instance = NaverLogin._();

  const NaverLogin._();

  Future<void> init({
    @required String clientId,
    @required String clientSecret,
    @required String clientName,
  }) async {
    await channel.invokeMethod('init', {
      'clientId': clientId,
      'clientSecret': clientSecret,
      'clientName': clientName,
    });
  }

  Future<Credential> start() async {
    final res = await channel.invokeMethod('start');
    return Credential.fromJson(res);
  }
}

@immutable
class Credential {
  final String accessToken;
  final String refreshToken;
  final int expiresAt;
  final String tokenType;

  @visibleForTesting
  Credential({
    @required this.accessToken,
    @required this.refreshToken,
    @required this.expiresAt,
    @required this.tokenType,
  });

  Credential.fromJson(Map<String, dynamic> data)
      : this(
          accessToken: data['accessToken'],
          refreshToken: data['refreshToken'],
          expiresAt: data['expiresAt'],
          tokenType: data['tokenType'],
        );
}
