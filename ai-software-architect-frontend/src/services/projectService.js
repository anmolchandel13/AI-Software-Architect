import api from './api';

/**
 * Service orchestrating CRUD operations and download flows for project architectures.
 */
export const projectService = {
  /**
   * Generates a new software architecture.
   */
  generateProject: async (projectIdea) => {
    const response = await api.post('/projects/generate', { projectIdea });
    return response.data;
  },

  /**
   * Fetches single project detail with 19 architecture sections.
   */
  getProjectById: async (id) => {
    const response = await api.get(`/projects/${id}`);
    return response.data;
  },

  /**
   * Fetches the listing of user projects (metadata only).
   */
  getAllProjects: async () => {
    const response = await api.get('/projects');
    return response.data;
  },

  /**
   * Filters user projects by keyword.
   */
  searchProjects: async (keyword) => {
    const response = await api.get('/projects/search', { params: { keyword } });
    return response.data;
  },

  /**
   * Deletes a project.
   */
  deleteProject: async (id) => {
    const response = await api.delete(`/projects/${id}`);
    return response.data;
  },

  /**
   * Downloads report in Markdown, JSON, or PDF format.
   * Fetches the file stream directly using Axios arraybuffer response.
   */
  downloadExport: async (id, format) => {
    const response = await api.get(`/projects/${id}/export/${format}`, {
      responseType: 'blob'
    });
    
    // Trigger browser file download directly
    const blob = new Blob([response.data], { type: response.headers['content-type'] });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = `report_${id}.${format === 'markdown' ? 'md' : format}`;
    link.click();
    window.URL.revokeObjectURL(link.href);
  }
};
