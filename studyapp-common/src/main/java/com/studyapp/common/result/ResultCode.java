package com.studyapp.common.result;

import lombok.Getter;

/**
 * 响应状态码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未登录或Token已过期"),
    TOKEN_INVALID(1002, "Token无效"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    WX_LOGIN_ERROR(1004, "微信登录失败"),
    ADMIN_LOGIN_ERROR(1005, "用户名或密码错误"),

    // 参数相关 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "缺少必要参数"),
    PARAM_INVALID(2003, "参数格式不正确"),

    // 业务相关 3xxx
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_DISABLED(3002, "用户已被禁用"),
    MAJOR_NOT_SELECTED(3003, "请先选择考研专业"),
    QUESTION_NOT_FOUND(3004, "题目不存在"),
    COURSE_NOT_FOUND(3005, "课程不存在"),
    EXAM_NOT_FOUND(3006, "试卷不存在"),
    EXAM_SUBMITTED(3007, "考试已交卷"),
    POST_NOT_FOUND(3008, "帖子不存在"),
    ALREADY_CHECKED_IN(3009, "今日已打卡"),

    // 权限相关 4xxx
    FORBIDDEN(4001, "没有操作权限"),
    ACCESS_DENIED(4002, "访问被拒绝"),

    // 系统相关 5xxx
    SYSTEM_ERROR(5001, "系统繁忙，请稍后重试"),
    SERVICE_UNAVAILABLE(5002, "服务不可用");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
