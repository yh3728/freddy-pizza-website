package com.freddypizza.website.service.admin

import com.freddypizza.website.entity.StaffEntity
import com.freddypizza.website.enums.StaffRole
import com.freddypizza.website.repository.StaffRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
@Transactional
class StaffServiceTest
    @Autowired
    constructor(
        private val underTest: StaffService,
        private val staffRepository: StaffRepository,
        private val encoder: BCryptPasswordEncoder,
    ) {
        private lateinit var staff1: StaffEntity
        private lateinit var staff2: StaffEntity
	
        private val password = "password"

        @BeforeEach
        fun setUp() {
            staff1 = StaffEntity(username = "staffuser1", password = password, role = StaffRole.COOK)
            staff2 = StaffEntity(username = "staffuser2", password = password, role = StaffRole.DELIVERY)
        }

        /**
         * Тест проверяет, что метод addStaff сохраняет сотрудника в базу данных с захешированным паролем.
         */
        @Test
        fun `test that addStaff saves the staff into the database with encoded password`() {
            val savedStaff = underTest.addStaff(staff1)
	
            assertThat(savedStaff).isNotNull()
            assertThat(savedStaff.id).isNotNull()
            assertThat(savedStaff.username).isEqualTo(staff1.username)
            assertThat(encoder.matches(password, savedStaff.password)).isTrue()
	
            val recalledStaff = staffRepository.findByIdOrNull(savedStaff.id)
            assertThat(recalledStaff).isNotNull()
            assertThat(recalledStaff).isEqualTo(savedStaff)
        }

        /**
         * Тест проверяет случай попытки добавить сотрудника с уже существующим username.
         * Ожидается, что будет выброшено исключение IllegalArgumentException.
         */
        @Test
        fun `test that adding existing username throws IllegalArgumentException`() {
            underTest.addStaff(staff1)
	
            assertThrows<IllegalArgumentException> {
                underTest.addStaff(staff1.copy(id = 0L))
            }
        }

        /**
         * Тест проверяет, что метод getAllStaff возвращает пустой список, если нет сотрудников.
         */
        @Test
        fun `test that getAllStaff returns empty list when no staff in the database`() {
            assertThat(underTest.getAllStaff()).isEmpty()
        }

        /**
         * Тест проверяет, что метод getAllStaff возвращает всех сотрудников, когда они есть в базе данных.
         */
        @Test
        fun `test that getAllStaff returns list of staff when staff exists in the database`() {
            val savedStaffList = listOf(staffRepository.save(staff1), staffRepository.save(staff2))
	
            assertThat(underTest.getAllStaff()).isEqualTo(savedStaffList)
        }

        /**
         * Тест проверяет, что метод getStaffById возвращает сотрудника, если он существует.
         */
        @Test
        fun `test that getStaffById returns staff when staff exists`() {
            val savedStaff = staffRepository.save(staff1)
	
            val foundStaff = underTest.getStaffById(savedStaff.id)
	
            assertThat(foundStaff).isEqualTo(savedStaff)
        }

        /**
         * Тест проверяет, что метод getStaffById возвращает null, если сотрудник не найден.
         */
        @Test
        fun `test that getStaffById returns null when staff does not exist`() {
            val foundStaff = underTest.getStaffById(999L)
	
            assertThat(foundStaff).isNull()
        }

        /**
         * Тест проверяет, что метод getStaffByUsername возвращает сотрудника, если он существует.
         */
        @Test
        fun `test that getStaffByUsername returns staff when staff exists`() {
            staffRepository.save(staff1)
	
            val foundStaff = underTest.getStaffByUsername(staff1.username)
	
            assertThat(foundStaff).isNotNull()
            assertThat(foundStaff!!.username).isEqualTo(staff1.username)
        }

        /**
         * Тест проверяет, что метод getStaffByUsername возвращает null, если сотрудник не найден.
         */
        @Test
        fun `test that getStaffByUsername returns null when staff does not exist`() {
            val foundStaff = underTest.getStaffByUsername("non_existing_username")
	
            assertThat(foundStaff).isNull()
        }

        /**
         * Тест проверяет удаление сотрудника по id.
         * Ожидается, что после удаления сотрудник больше не будет найден в базе данных.
         */
        @Test
        fun `test that deleteStaff removes staff from database`() {
            val savedStaff = staffRepository.save(staff1)
	
            underTest.deleteStaff(savedStaff.id)
	
            val deletedStaff = staffRepository.findByIdOrNull(savedStaff.id)
            assertThat(deletedStaff).isNull()
        }
    }
