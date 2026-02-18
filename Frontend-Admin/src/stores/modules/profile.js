import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getExperienceList,
  createExperience,
  updateExperience,
  deleteExperiences
} from '@/api/experience'
import {
  getSkillList,
  createSkill,
  updateSkill,
  deleteSkills
} from '@/api/skill'
import {
  getSocialMediaList,
  createSocialMedia,
  updateSocialMedia,
  deleteSocialMedias
} from '@/api/socialMedia'

export const useProfileStore = defineStore('profile', () => {
  const experiences = ref([])
  const skills = ref([])
  const socialMedias = ref([])
  const loading = ref(false)

  /* ---- Experience ---- */
  /** @param {number|undefined} type */
  const fetchExperiences = async (type) => {
    loading.value = true
    try {
      const res = await getExperienceList(type)
      experiences.value = res.data ?? []
    } finally {
      loading.value = false
    }
  }

  const saveExperience = async (data) => {
    if (data.id) {
      await updateExperience(data)
    } else {
      await createExperience(data)
    }
  }

  /** @param {number[]} ids */
  const removeExperiences = async (ids) => await deleteExperiences(ids)

  /* ---- Skill ---- */
  const fetchSkills = async () => {
    loading.value = true
    try {
      const res = await getSkillList()
      skills.value = res.data ?? []
    } finally {
      loading.value = false
    }
  }

  const saveSkill = async (data) => {
    if (data.id) {
      await updateSkill(data)
    } else {
      await createSkill(data)
    }
  }

  /** @param {number[]} ids */
  const removeSkills = async (ids) => await deleteSkills(ids)

  /* ---- Social Media ---- */
  const fetchSocialMedias = async () => {
    loading.value = true
    try {
      const res = await getSocialMediaList()
      socialMedias.value = res.data ?? []
    } finally {
      loading.value = false
    }
  }

  const saveSocialMedia = async (data) => {
    if (data.id) {
      await updateSocialMedia(data)
    } else {
      await createSocialMedia(data)
    }
  }

  /** @param {number[]} ids */
  const removeSocialMedias = async (ids) => await deleteSocialMedias(ids)

  return {
    experiences,
    skills,
    socialMedias,
    loading,
    fetchExperiences,
    saveExperience,
    removeExperiences,
    fetchSkills,
    saveSkill,
    removeSkills,
    fetchSocialMedias,
    saveSocialMedia,
    removeSocialMedias
  }
})
