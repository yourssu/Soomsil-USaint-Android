package com.yourssu.soomsil.usaint.domain.interfaces

/**
 * Worker 스케줄링을 위한 인터페이스
 * Domain 레이어에서 프레임워크 독립적으로 사용하기 위한 추상화
 */
interface WorkerScheduler {
    /**
     * Worker를 스케줄에 추가합니다.
     */
    fun enqueue()

    /**
     * Worker를 스케줄에서 제거합니다.
     */
    fun dequeue()
}

